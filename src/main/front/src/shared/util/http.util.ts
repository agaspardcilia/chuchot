import { Result } from '../model/result.model';
import { applicationEnv } from './env.util';

type MethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';
type BodyType = 'json' | 'form-data' | 'raw-text';
const getPath = (path: string): string => applicationEnv.apiBasePath + path;

const performRequest = async <R>(
    path: string, method: MethodType,
    body: any,
    bodyType: BodyType = 'json',
    responseBodyType: BodyType = 'json',
    prefixWithApiUri: boolean = true
): Promise<Result<R>> => {
    let actualBody;
    switch (bodyType) {
        case 'json':
            actualBody = body && JSON.stringify(body);
            break;
        case 'form-data':
        case 'raw-text':
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
        prefixWithApiUri ? getPath(path) : path,
        {
            method: method,
            mode: 'cors',
            headers,
            body: actualBody
        }
    );

    let responseBody;
    switch (responseBodyType) {
        case "json":
            responseBody = await response.json();
            break;
        case "form-data":
        case "raw-text":
            responseBody = await response.text();
            break;
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

const performGet = async <R>(path: string, bodyType: BodyType = 'json', prefixWithApiUri: boolean = true): Promise<Result<R>> =>
    performRequest(path, 'GET', undefined, undefined, bodyType, prefixWithApiUri);
const performDelete = async <R>(path: string, prefixWithApiUri: boolean = true): Promise<Result<R>> =>
    performRequest(path, 'DELETE', undefined, 'json', 'json', prefixWithApiUri);
const performPost = async <R>(path: string, body: any = {}, bodyType: BodyType = 'json', prefixWithApiUri: boolean = true): Promise<Result<R>> =>
    performRequest(path, 'POST', body, bodyType, 'json', prefixWithApiUri);

const getEventSource = (path: string): EventSource => new EventSource(getPath(path));

export const Http = {
    get: performGet,
    post: performPost,
    delete: performDelete,
    sse: getEventSource
};
