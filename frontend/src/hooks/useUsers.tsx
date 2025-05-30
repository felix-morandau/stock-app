import { useState, useEffect, useCallback } from 'react'
import axios from 'axios'
import type User from "../models/user.ts";
import {API_BASE_URL} from "../constants/api.ts";

function getAuthHeaders() {
    const token = sessionStorage.getItem('token') || ''
    return { headers: { Authorization: `Bearer ${token}` } }
}

export function useUsers() {
    const [users, setUsers] = useState<User[]>([])
    const [loading, setLoading] = useState<boolean>(false)
    const [error, setError] = useState<Error | null>(null)

    const fetchUsers = useCallback(() => {
        setLoading(true)
        setError(null)
        axios
            .get<User[]>(`${API_BASE_URL}/user/all`, getAuthHeaders())
            .then(res => setUsers(res.data))
            .catch(err => setError(err))
            .finally(() => setLoading(false))
    }, [])

    const deleteUser = useCallback((id: string) => {
        return axios
            .delete(`${API_BASE_URL}/user/${id}`, getAuthHeaders())
            .then(() => {
                setUsers(prev => prev.filter(u => u.id !== id))
            })
    }, [])

    useEffect(() => {
        fetchUsers()
    }, [fetchUsers])

    return {
        users,
        loading,
        error,
        fetchUsers,
        deleteUser,
    }
}
