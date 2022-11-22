import { PropsWithChildren, ReactElement } from "react";
import Navbar from "../Navbar";

const MainLayout = ({ children }: PropsWithChildren) => (
  <div>
    <Navbar></Navbar>
    <div className="container main">{children}</div>
  </div>
);

export const layout = (page: ReactElement) => <MainLayout>{page}</MainLayout>;

export default MainLayout;
