export const NodeUtilities = {
  formatId(id: string | undefined | null) {
    if (!id) return undefined;

    return [id.substring(0, 3), id.substring(3, 8), id.substring(8)].join('-');
  },
};
