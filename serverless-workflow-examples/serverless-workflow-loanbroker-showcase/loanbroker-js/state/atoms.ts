import { atom } from "recoil";
import Quote from "../model/Quote";
import QuoteResponse from "../model/QuoteResponse";
import { WorkflowdataReponse } from "../model/WorkflowResponse";

export const quotesListState = atom<QuoteResponse[]>({
  key: "QuotesList",
  default: [],
});

export const pendingState = atom<WorkflowdataReponse[]>({
  key: "PendingList",
  default: [],
});
