export interface Patient{
    id: number;
    firstName: string;
    lastName: string;
    birthDate: string;
    gender: 'M' | 'F';
    address: string;
    phoneNumber: string;
}

export interface PatientCreateDto {
    firstName: string;
    lastName: string;
    birthDate: string;
    gender: 'M' | 'F';
    address?: string;
    phoneNumber?: string;
}

export interface PatientUpdateDto {
    firstName?: string;
    lastName?: string;
    birthDate?: string;
    gender?: 'M' | 'F';
    address?: string | null;
    phoneNumber?: string | null;
}