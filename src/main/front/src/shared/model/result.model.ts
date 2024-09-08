/**
 * TODO: comment me!
 * TODO: should include an error when need be.
 */
export type Result<R> = {
    status: 'success',
    result: R,
} | {
    status: 'failure',
    httpStatus: string,
    httpStatusCode: number,
    message: string,
    timestamp: string,
};
