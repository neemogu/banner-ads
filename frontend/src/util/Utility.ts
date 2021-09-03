export function capitalizeFirstLetter(str: string|undefined) {
    if (str === undefined) {
        return undefined;
    }
    return str.charAt(0).toUpperCase() + str.slice(1);
}
