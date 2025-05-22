import {type FormEvent, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import { API_BASE_URL } from '../../constants/api.ts';


const COUNTRIES = [
    'USA',
    'CANADA',
    'UK',
    'GERMANY',
    'FRANCE',
    'ROMANIA',
    'SPAIN',
] as const;
type Country = (typeof COUNTRIES)[number];

interface CreateUserDto {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    age: number;
    country: Country;
    type: Role;
}

type Role = 'CLIENT' | 'ADMIN';

export default function RegisterPage() {
    const [firstName, setFirstName] = useState('');
    const [lastName,  setLastName]  = useState('');
    const [email,     setEmail]     = useState('');
    const [password,  setPassword]  = useState('');
    const [confirmPw, setConfirmPw] = useState('');
    const [age,       setAge]       = useState('');
    const [country,   setCountry]   = useState<Country>('USA');

    const [error,     setError]     = useState('');
    const [loading,   setLoading]   = useState(false);

    const navigate = useNavigate();

    const { role: roleParam } = useParams<{ role?: Role }>();
    const type: Role = roleParam ?? 'CLIENT';

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        setError('');

        if (password !== confirmPw) {
            setError('Passwords do not match');
            return;
        }
        if (!Number(age) || Number(age) < 18) {
            setError('Enter a valid age (18+)');
            return;
        }

        const dto: CreateUserDto = {
            firstName,
            lastName,
            email,
            password,
            age: Number(age),
            country,
            type,
        };

        try {
            setLoading(true);
            await axios.post(`${API_BASE_URL}/user`, dto, {
                headers: {'Content-Type': 'application/json'},
            });

            navigate('/login');
        } catch (err: unknown) {
            if (axios.isAxiosError(err) && err.response) {
                const { status, data } = err.response;

                if (status === 400 && typeof data === 'object') {
                    const merged = Object.values<string>(data).join('. ');
                    setError(merged);
                } else if (status === 409 && typeof data=== 'object') {
                    setError('Email already in use.');
                } else {
                    const msg =
                        (data as any)?.errorMessage ??
                        err.response.statusText ??
                        'Registration failed';
                    setError(msg);
                }
            } else {
                setError('Unexpected error – please try again later');
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="register-container">
            <h1>Create account</h1>

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="fn">First name</label>
                    <input id="fn" value={firstName} onChange={e => setFirstName(e.target.value)} required />
                </div>

                <div>
                    <label htmlFor="ln">Last name</label>
                    <input id="ln" value={lastName} onChange={e => setLastName(e.target.value)} required />
                </div>

                <div>
                    <label htmlFor="em">E-mail</label>
                    <input
                        id="em"
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="pw">Password</label>
                    <input
                        id="pw"
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="cpw">Confirm password</label>
                    <input
                        id="cpw"
                        type="password"
                        value={confirmPw}
                        onChange={e => setConfirmPw(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="age">Age</label>
                    <input
                        id="age"
                        type="number"
                        min={18}
                        value={age}
                        onChange={e => setAge(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="cty">Country</label>
                    <select id="cty" value={country} onChange={e => setCountry(e.target.value as Country)}>
                        {COUNTRIES.map(c => (
                            <option key={c} value={c}>
                                {c}
                            </option>
                        ))}
                    </select>
                </div>

                {error && <p className="error-message">{error}</p>}

                <button className="login" type="submit" disabled={loading}>
                    {loading ? 'Creating…' : 'Register'}
                </button>
            </form>

            <p style={{ marginTop: 16 }}>
                Already have an account?{' '}
                <button type="button" className="ghost" onClick={() => navigate('/login')}>
                    Sign in
                </button>
            </p>
        </div>
    );
}
