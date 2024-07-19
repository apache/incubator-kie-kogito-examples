import { layout } from "../components/layout/MainLayout";
import { NextPageWithLayout } from "./_app";

const Home: NextPageWithLayout = () => {
  return (
    <div>
      <h1>A Demo Application</h1>
    </div>
  );
};

Home.getLayout = layout;

export default Home;
