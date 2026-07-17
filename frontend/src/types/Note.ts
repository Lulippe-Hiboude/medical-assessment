export interface Note{
    id: string;
    patientId: number;
    content: string;
    createdAt:string;
}

export interface NoteCreateDto{
    patientId: number;
    content: string;
}