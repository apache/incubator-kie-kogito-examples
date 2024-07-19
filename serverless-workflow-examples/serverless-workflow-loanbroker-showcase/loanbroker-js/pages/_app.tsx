import { NextPage } from "next";
import type { AppProps } from "next/app";
import { ReactElement, ReactNode, useEffect } from "react";
import { RecoilRoot, useRecoilState } from "recoil";
import QuoteResponse from "../model/QuoteResponse";
import Communication from "../services/Communication";
import { pendingState, quotesListState } from "../state/atoms";
import "../styles/main.scss";

export type NextPageWithLayout<P = {}, IP = P> = NextPage<P, IP> & {
  getLayout?: (page: ReactElement) => ReactNode;
};

type AppPropsWithLayout = AppProps & {
  Component: NextPageWithLayout;
};

var connected = false;
var socket: WebSocket;

const WebSocketComponent = () => {
  const [quotes, setQuotes] = useRecoilState(quotesListState);
  const [pending, setPending] = useRecoilState(pendingState);
  useEffect(() => {
    if (!connected) {
      socket = new WebSocket(
        process.env.NEXT_PUBLIC_WEBSOCKET_URL ||
          "ws://localhost:8080/socket/quote/new"
      );
    }

    socket.onopen = function () {
      console.log("Connected to backend");
      connected = true;
    };
    socket.onmessage = function (m) {
      const quote: QuoteResponse = JSON.parse(m.data);
      setQuotes((oldState) => [...oldState, quote]);
      setPending((old) =>
        old.filter((elem) => elem.id !== quote.loanRequestId)
      );
    };
    socket.onerror = (e) => {
      console.log("WebSocket error: ", e);
    };
  });
  return <></>;
};

export default function MyApp({ Component, pageProps }: AppPropsWithLayout) {
  useEffect(() => {
    require("bootstrap/dist/js/bootstrap.bundle.min.js");
  });
  // Use the layout defined at the page level, if available
  const getLayout = Component.getLayout ?? ((page) => page);

  return getLayout(
    <RecoilRoot>
      <WebSocketComponent />
      <Component {...pageProps} />
    </RecoilRoot>
  );
}
