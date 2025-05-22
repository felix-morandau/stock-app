import type {StockCandle} from "./stockCandle.ts";

export interface StockData{
    date: string,
    candle: StockCandle
}