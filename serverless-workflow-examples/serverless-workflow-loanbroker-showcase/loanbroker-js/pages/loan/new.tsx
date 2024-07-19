import { ErrorMessage, Field, Form, Formik } from "formik";
import { useRouter } from "next/router";
import { useRecoilState } from "recoil";
import { layout } from "../../components/layout/MainLayout";
import LoanRequest from "../../model/LoanRequest";
import Communication from "../../services/Communication";
import { pendingState } from "../../state/atoms";
import { NextPageWithLayout } from "../_app";

const initialValues: LoanRequest = { SSN: "", amount: 0, term: 0 };

const NewLoan: NextPageWithLayout = () => {
  const router = useRouter();
  const [pending, setPending] = useRecoilState(pendingState);
  return (
    <div className="animated fadeIn">
      <h1>Loan Application Form</h1>

      <Formik
        initialValues={initialValues}
        validate={(values) => {
          const errors = {};
          return errors;
        }}
        onSubmit={(values, { setSubmitting }) => {
          Communication.createLoan(values)
            .then((res: any) => {
              setSubmitting(false);
              setPending((old) => [...old, res.data]);
              router.push("/loan");
            })
            .catch((error: any) => {
              setSubmitting(false);
            });
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <div className="mb-3">
              <label>SSN</label>
              <Field
                className="form-control"
                type="text"
                placeholder="123-45-6789"
                name="SSN"
              />
              <ErrorMessage name="SSN" component="div" />
            </div>
            <div className="mb-3">
              <label>Term</label>
              <Field
                className="form-control"
                type="number"
                placeholder="30"
                name="term"
              />
              <ErrorMessage name="term" component="div" />
            </div>
            <div className="mb-3">
              <label>Amount</label>
              <Field
                className="form-control"
                type="number"
                placeholder="50000"
                name="amount"
              />
              <ErrorMessage name="amount" component="div" />
            </div>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={isSubmitting}
            >
              <span
                className={`spinner-border spinner-border-sm ${
                  isSubmitting ? "" : "visually-hidden"
                }`}
                role="status"
                aria-hidden="true"
              ></span>{" "}
              Apply
            </button>
          </Form>
        )}
      </Formik>
    </div>
  );
};

NewLoan.getLayout = layout;

export default NewLoan;
