import { Result } from '../model/result.model';
import { applicationEnv } from './env.util';
import { localStorageUtil } from './local-storage.util';

type MethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';
const getPath = (path: string): string => applicationEnv.apiBasePath + path;

console.log('Path', getPath(''));

const performRequest = async <R>(path: string, method: MethodType, body: any): Promise<Result<R>> => {
    const response = await fetch(
        getPath(path),
        {
            method: method,
            mode: 'cors',
            headers: {
                'Content-type': 'application/json;charset=UTF-8',
                'Authorization': `Bearer ${localStorageUtil.read('jwt')}`
            },
            body: body ? JSON.stringify(body) : undefined
        }
    );

    let responseBody;
    try {
        responseBody = await response.json();
    } catch (e) {
        responseBody = undefined;
    }

    if (response.ok) {
        return {
            status: 'success',
            result: responseBody
        };
    } else {
        // TODO: add some toasting there so I don't have to deal with errors.
        return { status: 'failure', ...responseBody };
    }
};

const performGet = async <R>(path: string): Promise<Result<R>> => performRequest(path, 'GET', undefined);
const performDelete = async <R>(path: string): Promise<Result<R>> => performRequest(path, 'DELETE', undefined);
const performPost = async <R>(path: string, body: any = {}): Promise<Result<R>> => performRequest(path, 'POST', body);

export const Http = {
    get: performGet,
    post: performPost,
    delete: performDelete,
};
