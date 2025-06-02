import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import type User from "../models/user.ts";
import { API_BASE_URL } from "../constants/api.ts";

function getAuthHeaders() {
    const token = sessionStorage.getItem('token') || '';
    return { headers: { Authorization: `Bearer ${token}` } };
}

export function useUsers() {
    const [users, setUsers] = useState<User[]>([]);
    const [user, setUser] = useState<User | null>(null); // State for a single user
    const [loading, setLoading] = useState<boolean>(false);
    const [userLoading, setUserLoading] = useState<boolean>(false); // Separate loading state for single user
    const [error, setError] = useState<Error | null>(null);
    const [userError, setUserError] = useState<Error | null>(null); // Separate error state for single user

    const fetchUsers = useCallback(() => {
        setLoading(true);
        setError(null);
        axios
            .get<User[]>(`${API_BASE_URL}/user/all`, getAuthHeaders())
            .then(res => setUsers(res.data))
            .catch(err => setError(err))
            .finally(() => setLoading(false));
    }, []);

    const fetchUserById = useCallback((id: string) => {
        setUserLoading(true);
        setUserError(null);
        return axios
            .get<User>(`${API_BASE_URL}/user/${id}`, getAuthHeaders())
            .then(res => {
                setUser(res.data);
                return res.data;
            })
            .catch(err => {
                setUserError(err);
                throw err;
            })
            .finally(() => setUserLoading(false));
    }, []);

    const deleteUser = useCallback((id: string) => {
        return axios
            .delete(`${API_BASE_URL}/user/${id}`, getAuthHeaders())
            .then(() => {
                setUsers(prev => prev.filter(u => u.id !== id));
            });
    }, []);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    return {
        users,
        user, // Single user state
        loading,
        userLoading, // Loading state for single user
        error,
        userError, // Error state for single user
        fetchUsers,
        fetchUserById, // New method to fetch a single user
        deleteUser,
    };
}