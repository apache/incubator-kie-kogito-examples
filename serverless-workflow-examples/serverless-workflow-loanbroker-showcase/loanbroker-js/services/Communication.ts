import LoanRequest from "../model/LoanRequest";

const axios = require("axios").default;

const Communication = {
  createLoan(loan: LoanRequest) {
    return axios.post(
      process.env.NEXT_PUBLIC_WORKFLOW_URL,
      { workflowdata: loan },
      {
        headers: {
          "content-type": "application/json; charset=UTF-8",
        },
      }
    );
  },
};

export default Communication;
