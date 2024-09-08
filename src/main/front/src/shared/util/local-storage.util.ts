
type Keys = 'jwt';

export const localStorageUtil = {
    read: (key: Keys): string | null => localStorage.getItem(key),
    write: (key: Keys, value: string): void => localStorage.setItem(key, value),
    remove: (key: Keys): void => localStorage.removeItem(key)
};
