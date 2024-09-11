import React from 'react';
import { JobDescription } from '../../shared/model/job.model';
import { SubmitHandler, useForm } from 'react-hook-form';
import { Item } from '../../shared/model/item.model';
import './update-job.css';

interface UpdateJobProps {
    items: Item[];
    initialJob?: JobDescription;
    onSave: (description: JobDescription, id?: string) => void;
}

interface JobForm {
    name: string;
    item: string;
    language: string;
    model: string;
    task: string;
}

export const UpdateJobComponent: React.FC<UpdateJobProps> = ({items, onSave}) => {
    const { register, handleSubmit, formState: { errors } } = useForm<JobForm>();

    const onSubmit: SubmitHandler<JobForm> = data => {
        const { name, task, model, language, item } = data;
        const toSave: JobDescription = {
            name,
            sourceItemName: item,
            parameters: {
                task,
                model,
                language
            }
        };
        onSave(toSave);
    }

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="input-element">
                <label htmlFor="name">Name </label>
                <input {...register('name', { required: true, maxLength: 64 })} />
                {errors.item && <span>Invalid field</span>}

            </div>

            <div className="input-element">
                <label htmlFor="item">File </label>
                <select {...register('item', { required: true })}>
                    <option value="" />
                    {items.map(i => (<option key={i.name} value={i.name}>{i.name}</option>))}
                </select>
                {errors.item && <span>Item cannot be empty!</span>}
            </div>

            <div className="input-element">
                <label htmlFor="language">Language </label>
                <select {...register('language')}>
                    <option value="ENGLISH">English</option>
                    <option value="FRENCH">French</option>
                </select>
            </div>

            <div className="input-element">
                <label htmlFor="model">Model </label>
                <select {...register('model')}>
                    <option value="TINY">Tiny</option>
                </select>
            </div>

            <div className="input-element">
                <label htmlFor="task">Task </label>
                <select {...register('task')}>
                    <option value="TRANSCRIBE">Transcribe</option>
                </select>
            </div>
            <div className="submit-element">
                <button type="submit">Create Job</button>
            </div>
        </form>
    );
}
