import { useState, useEffect, useCallback, useRef } from 'react';
import type { Portfolio } from "../models/portfolio.ts";
import { API_BASE_URL } from "../constants/api.ts";
import axios from "axios";
import type { PortfolioStatsDTO } from '../models/stats.ts';

interface UsePortfoliosResult {
  portfolios: Portfolio[] | null;
  loading: boolean;
  error: string | null;
  fetchPortfolioStats: (portfolioId: string) => Promise<PortfolioStatsDTO>;
}

function getAuthHeaders() {
  const token = sessionStorage.getItem('token') || '';
  return { headers: { Authorization: `Bearer ${token}` } };
}

export function usePortfolios(): UsePortfoliosResult {
  const [portfolios, setPortfolios] = useState<Portfolio[] | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const fetchedStats = useRef<Set<string>>(new Set()); // tracks fetched stats

  // Fetch all portfolios once on mount
  useEffect(() => {
    let isStillMounted = true;
    setLoading(true);
    setError(null);

    const userId = sessionStorage.getItem('userId');
    if (!userId) {
      setError('Missing user ID');
      setLoading(false);
      return;
    }

    axios
        .get<Portfolio[]>(`${API_BASE_URL}/portfolio/all/${userId}`, getAuthHeaders())
        .then((res) => {
          if (isStillMounted) {
            setPortfolios(res.data);
            setLoading(false);
          }
        })
        .catch((err) => {
          if (isStillMounted) {
            setError(err.message || 'Failed to fetch portfolios');
            setLoading(false);
          }
        });

    return () => {
      isStillMounted = false;
    };
  }, []);

  const fetchPortfolioStats = useCallback(async (portfolioId: string): Promise<PortfolioStatsDTO> => {
    if (fetchedStats.current.has(portfolioId)) {
      throw new Error('Stats already fetched for this portfolio');
    }

    setLoading(true);
    setError(null);

    try {
      const response = await axios.get<PortfolioStatsDTO>(
          `${API_BASE_URL}/portfolio/${portfolioId}/stats`,
          getAuthHeaders()
      );

      setPortfolios(prev => {
        if (!prev) return prev;
        return prev.map(portfolio =>
            portfolio.id === portfolioId
                ? { ...portfolio, ...response.data }
                : portfolio
        );
      });

      fetchedStats.current.add(portfolioId);
      setLoading(false);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to fetch portfolio stats');
      setLoading(false);
      throw err;
    }
  }, []);

  return { portfolios, loading, error, fetchPortfolioStats };
}
