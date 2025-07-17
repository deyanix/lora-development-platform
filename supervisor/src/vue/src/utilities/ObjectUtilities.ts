export const ObjectUtilities = {
  common<T extends object>(arr: T[]): Partial<T> {
    const ref = arr.find((obj) => !!obj);
    if (!ref) return {};

    return Object.fromEntries(
      Object.entries(ref).filter(([key, value]) =>
        arr.every((obj) => obj[key as keyof T] === value),
      ),
    ) as Partial<T>;
  },
};
