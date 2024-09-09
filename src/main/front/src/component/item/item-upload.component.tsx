import React, { useState } from 'react';
import { useItemStore } from '../../shared/store/item-store.store';
import { tartine } from '../../shared/util/tartine';


export const ItemUploadComponent: React.FC = () => {
    const { uploadAccept, uploadItem } = useItemStore();
    const [error, setError] = useState<string>();
    const [locked, setLocked] = useState<boolean>(false);
    const [value, setValue] = useState<File>();


    const onInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        setLocked(true);
        const { files } = e.target;
        try {
            setError('');
            if (!files) {
                return;
            }
            if (files.length > 1) {
                setError('Can upload only one file at the time')
                return;
            }
            if (!files.length) {
                setError('Select a file');
                return;
            }
            setValue(files[0]);
        } finally {
            setLocked(false);
        }
    };

    const onUpload = async () => {
        if (!value) {
            tartine.info('No selected file')
            return;
        }
        try {
            const item = await uploadItem(value);
            tartine.success(`${item.name} has been uploaded with success`);
        } catch (e) {
            tartine.error('Upload failed');
        }

    };

    return (
        <>
            {uploadAccept
                ? (
                    <>
                        <label htmlFor="item" title="Upload file">📤</label>
                        {' '}
                        <input name="item"
                               type="file"
                               accept={uploadAccept}
                               onChange={onInput} disabled={locked}/>
                        {error ? <span>{error}</span> : undefined}
                        {value ? <button onClick={onUpload}>Upload</button>: undefined}
                    </>
                ) : undefined
            }

        </>
    );
};
