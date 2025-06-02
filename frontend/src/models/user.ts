import type {Country} from "./countries.ts";

export default interface User{
    id: string,
    firstName: string,
    lastName: string,
    email: string,
    age: number,
    country: Country,
    bio: string,
    type: 'ADMIN' | 'CLIENT',
    createdAt: string,
    balance: number
}