import { Result } from '../model/result.model';
import { applicationEnv } from './env.util';

type MethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';
type BodyType = 'json' | 'form-data';
const getPath = (path: string): string => applicationEnv.apiBasePath + path;

const performRequest = async <R>(path: string, method: MethodType, body: any, bodyType: BodyType = 'json'): Promise<Result<R>> => {
    let actualBody;
    switch (bodyType) {
        case 'json':
            actualBody = body && JSON.stringify(body);
            break;
        case 'form-data':
        default:
            actualBody = body;
            break;
    }

    let headers;
    if (bodyType === 'form-data') {
        headers = {};
    } else {
        headers = {'Content-type': 'application/json;charset=UTF-8'};
    }

    const response = await fetch(
        getPath(path),
        {
            method: method,
            mode: 'cors',
            headers,
            body: actualBody
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
        return { status: 'failure', ...responseBody };
    }
};

const performGet = async <R>(path: string): Promise<Result<R>> => performRequest(path, 'GET', undefined);
const performDelete = async <R>(path: string): Promise<Result<R>> => performRequest(path, 'DELETE', undefined);
const performPost = async <R>(path: string, body: any = {}, bodyType: BodyType = 'json'): Promise<Result<R>> => performRequest(path, 'POST', body, bodyType);

const getEventSource = (path: string): EventSource => new EventSource(getPath(path));

export const Http = {
    get: performGet,
    post: performPost,
    delete: performDelete,
    sse: getEventSource
};
