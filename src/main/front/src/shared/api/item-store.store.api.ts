import { Result } from '../model/result.model';
import { Item } from '../model/item.model';
import { Http } from '../util/http.util';

const basePath = '/store';

export const itemStoreStoreApi = {
    inventory: (): Promise<Result<Item[]>> => Http.get<Item[]>(`${basePath}/inventory`),
    jobInventory: (jobId: string): Promise<Result<Item[]>> => Http.get<Item[]>(`${basePath}/inventory/${jobId}`)
};
