import Credit from "./Credit";
import Quote from "./Quote";

export default interface QuoteResponse {
  loanRequestId: string;
  amount: number;
  term: number;
  credit: Credit;
  quotes: Quote[];
}
