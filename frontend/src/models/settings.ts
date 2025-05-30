import type {Language} from "./languages.ts";
import type {Currency} from "./currencies.ts";
import type {Theme} from "./themes.ts";

export interface Settings{
    language: Language,
    currency: Currency,
    theme:Theme
}