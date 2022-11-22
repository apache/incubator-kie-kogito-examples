import Link from "next/link";
import { useRouter } from "next/router";
import routes, { Route } from "../routes";

const NavItem = ({ route }: { route: Route }) => {
  const router = useRouter();
  return (
    <li className="nav-item">
      <Link href={route.link}>
        <a
          className={
            router.pathname === route.link ? "nav-link active" : "nav-link"
          }
          aria-current="page"
          href="#"
        >
          {route.name}
        </a>
      </Link>
    </li>
  );
};

const NavDropdown = ({ route }: { route: Route }) => (
  <li className="nav-item dropdown">
    <a
      className="nav-link dropdown-toggle"
      href="#"
      role="button"
      data-bs-toggle="dropdown"
      aria-expanded="false"
    >
      {route.name}
    </a>
    <ul className="dropdown-menu">
      {route.items?.map((item) => (
        <NavDropdownItem key={item.name} route={item}></NavDropdownItem>
      ))}
    </ul>
  </li>
);

const NavDropdownItem = ({ route }: { route: Route }) => (
  <li>
    <Link href={route.link}>
      <a className="dropdown-item" href="#">
        {route.name}
      </a>
    </Link>
  </li>
);

const getNavItem = (route: Route) => {
  if (route.items) {
    return <NavDropdown key={route.name} route={route}></NavDropdown>;
  } else {
    return <NavItem key={route.name} route={route}></NavItem>;
  }
};

const Navbar = () => (
  <nav className="navbar navbar-expand-lg navbar-light bg-primary">
    <div className="container">
      <a className="navbar-brand" href="#">
        <img
          src="/capital-bank.png"
          alt="Logo"
          width="250"
          height="40"
          className="d-inline-block align-text-top"
        />
      </a>
      <button
        className="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>
      <div className="collapse navbar-collapse" id="navbarSupportedContent">
        <ul className="navbar-nav me-auto mb-2 mb-lg-0">
          {routes.map((r) => getNavItem(r))}
        </ul>
      </div>
    </div>
  </nav>
);

export default Navbar;
