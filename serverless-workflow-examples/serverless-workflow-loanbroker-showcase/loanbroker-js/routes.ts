export interface Route {
  name: string;
  link: string;
  items?: Route[];
}

const routes: Route[] = [
  { name: "Home", link: "/" },
  {
    name: "Loan Application",
    link: "/loan",
    items: [
      {
        name: "My Loans",
        link: "/loan",
      },
      {
        name: "New Loan",
        link: "/loan/new",
      },
    ],
  },
  // {
  //   name: "Admin",
  //   link: "/admin",
  //   items: [],
  // },
];

export default routes;
