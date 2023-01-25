import Credit from "./Credit";

export interface WorkflowdataReponse {
  id: string;
  workflowdata: Workflowdata;
}

export interface Workflowdata {
  amount: number;
  credit: Credit;
  term: number;
}
