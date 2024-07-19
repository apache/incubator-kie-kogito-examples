import { useRecoilState } from "recoil";
import { layout } from "../../components/layout/MainLayout";
import QuoteResponse from "../../model/QuoteResponse";
import { pendingState, quotesListState } from "../../state/atoms";
import { NextPageWithLayout } from "../_app";

const Loans: NextPageWithLayout = () => {
  const [quotes, setQuotes] = useRecoilState(quotesListState);
  const [pending, setPending] = useRecoilState(pendingState);
  console.log(quotes);
  return (
    <div>
      <section className="mb-4">
        <h1>Pending Loans</h1>
        <div className="card shadow border-light">
          <div className="card-body">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th scope="col">#</th>
                  <th scope="col">SSN</th>
                  <th scope="col">Term</th>
                  <th scope="col">Amount</th>
                  <th scope="col">Score</th>
                  <th scope="col">History</th>
                </tr>
              </thead>
              <tbody>
                {pending.map((q) => (
                  <tr key={q.id}>
                    <th scope="row">{q.id}</th>
                    <td>{q.workflowdata.credit.SSN || "-"}</td>
                    <td>{q.workflowdata.term}</td>
                    <td>{q.workflowdata.amount}</td>
                    <td>{q.workflowdata.credit.score}</td>
                    <td>{q.workflowdata.credit.history}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <section>
        <h1>Completed Loans</h1>
        <div className="card shadow border-light">
          <div className="card-body">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th scope="col">#</th>
                  <th scope="col">SSN</th>
                  <th scope="col">Term</th>
                  <th scope="col">Amount</th>
                  <th scope="col">Score</th>
                  <th scope="col">History</th>
                </tr>
              </thead>
              <tbody>
                {quotes.map((q, i) => (
                  <>
                    <tr key={i}>
                      <th scope="row">{q.loanRequestId}</th>
                      <td>{q.credit.SSN}</td>
                      <td>{q.term}</td>
                      <td>{q.amount}</td>
                      <td>{q.credit.score}</td>
                      <td>{q.credit.history}</td>
                    </tr>
                    <tr>
                      <td colSpan={6}>
                        {q.quotes.map((b) => (
                          <div key={b.bankId} className="ps-5">
                            <b>{b.bankId}</b> : {b.rate}
                          </div>
                        ))}
                      </td>
                    </tr>
                  </>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
};

Loans.getLayout = layout;

export default Loans;
