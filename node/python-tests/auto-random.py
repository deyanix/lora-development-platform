import serial
import serial.tools.list_ports
import time
import threading

BAUD_RATE = 115200
VENDOR_ID = '10C4'  # '10C4' in hex is 4292 in decimal
PRODUCT_ID = 'EA60' # 'EA60' in hex is 59992 in decimal

def find_serial_ports(vendor_id, product_id):
    """
    Lists serial ports with specific vendor and product IDs.
    """
    found_ports = []
    for port in serial.tools.list_ports.comports():
        # pyserial returns vendorId and productId as integers
        if port.vid == int(vendor_id, 16) and port.pid == int(product_id, 16):
            found_ports.append(port.device)
    return found_ports

def read_from_port(port):
    """
    Continuously reads data from the serial port and prints it.
    """
    while True:
        if port.is_open:
            try:
                # Read until a newline character
                line = port.readline().decode('utf-8').strip()
                if line:
                    print(f"{port.port} << {line}")
            except serial.SerialException as e:
                print(f"Error reading from {port.port}: {e}")
                break
            except UnicodeDecodeError:
                print(f"Could not decode byte from {port.port}")
                continue
        else:
            break

def initialize_port(port, idx):
    """
    Sends initialization commands to the serial port.
    """
    commands = [
        '+ID?\n',
        '+FRQ=868000000\n',
        '+BW=3\n',
        '+SF=12\n',
        '+PWR=12\n',
        '+CRT=2\n',
        '+PRLEN=8\n',
        '+PYLEN=0\n',
        '+CRC=0\n',
        '+IIQ=1\n',
        '+STO=0\n',
        '+TXTO=3000\n',
        f'+MODE={"RX" if idx == 0 else "TX"}\n',
        '+INTERVAL=10000\n',
        '+MINDELTA=100\n',
        '+MAXDELTA=100\n',
        '+AUTO=RANDOM\n',
        '+FRQ?\n',
        '+BW?\n',
        '+SF?\n',
        '+PWR?\n',
        '+CRT?\n',
        '+PRLEN?\n',
        '+PYLEN?\n',
        '+CRC?\n',
        '+IIQ?\n',
        '+STO?\n',
        '+TXTO?\n',
        '+MODE?\n',
        '+PUSH\n',
        '+TOA=4?\n',
        '+AUTO?\n',
        '+INTERVAL?\n',
        '+MINDELTA?\n',
        '+MAXDELTA?\n'
    ]
    for cmd in commands:
        port.write(cmd.encode('utf-8'))
        time.sleep(0.05) # equivalent to delay(100)

def main():
    """
    Main function to set up and manage serial communications.
    """
    port_paths = find_serial_ports(VENDOR_ID, PRODUCT_ID)
    serial_ports = []

    if not port_paths:
        print(f"No serial ports found with Vendor ID: {VENDOR_ID} and Product ID: {PRODUCT_ID}")
        return

    print('Used ports:', ', '.join(port_paths))

    for path in port_paths:
        try:
            ser = serial.Serial(path, BAUD_RATE, timeout=1)
            serial_ports.append(ser)
            # Start a separate thread for reading from each port
            read_thread = threading.Thread(target=read_from_port, args=(ser,))
            read_thread.daemon = True # Allow main program to exit even if threads are running
            read_thread.start()
        except serial.SerialException as e:
            print(f"Could not open serial port {path}: {e}")

    if not serial_ports:
        print("No serial ports were successfully opened.")
        return

    # Initialize ports
    for idx, port in enumerate(serial_ports):
        initialize_port(port, idx)

    # Main loop for periodic commands
    def periodic_tasks():
        if serial_ports:
            port_tx = serial_ports[0]
            try:
                port_tx.write(b'+TX=5,abcdef\n')
                time.sleep(0.1)

                for port_rx in serial_ports:
                    port_rx.write(b'+STA?\n')
                    time.sleep(0.1)
            except serial.SerialException as e:
                print(f"Error during periodic task: {e}")
                # Potentially re-establish connection or handle error
            except Exception as e:
                print(f"An unexpected error occurred during periodic task: {e}")

        # Schedule the next run
        threading.Timer(3, periodic_tasks).start()

    # Start the periodic tasks
    #periodic_tasks()

    try:
        # Keep the main thread alive to allow background threads to run
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\nExiting program.")
    finally:
        for port in serial_ports:
            if port.is_open:
                port.close()
                print(f"Closed port {port.port}")

if __name__ == "__main__":
    main()