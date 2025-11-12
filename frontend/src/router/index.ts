import { createRouter, createWebHistory } from "vue-router";
import UserProfile from "../components/UserProfile.vue";
import store from "../store";
import Login from "../views/Login.vue";
import CardApplicationPage from "../views/Users/CardApplicationPage.vue";
import CardApplicationHistoryPage from "../views/Users/CardApplicationHistoryPage.vue";
import LandingPage from "@/views/LandingPage.vue";
 
const Dashboard = () => import("../views/Users/Dashboard.vue");
const CardDetail = () => import("../views/Users/CardDetail.vue");
const TransactionHistory = () => import("../views/Users/TransactionHistory.vue");
const CreateTransaction = () => import("../views/Users/CreateTransaction.vue");

const routes = [
  {
    path: "/",
    name: "LandingPage",
    component: LandingPage,
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/register",
    name: "Register",
    component: Login,
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/card/:id",
    alias: "/cards/:id",
    name: "CardDetail",
    component: CardDetail,
    props: true,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/profile",
    name: "Profile",
    component: UserProfile,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/logout",
    name: "Logout",
    component: Login,
    meta: { requiresAuth: true },
  },
  {
    path: "/transactions",
    name: "TransactionHistory",
    component: TransactionHistory,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/create-transaction",
    name: "CreateTransaction",
    component: CreateTransaction,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/cards",
    name: "MyCards",
    component: () => import("../views/Users/MyCards.vue"),
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/admin-dashboard",
    name: "AdminDashboard",
    component: () => import("../views/AdminDashboard.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin-review",
    name: "AdminReviewCenter",
    component: () => import("../views/AdminReviewCenter.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/apply-card",
    name: "ApplyCard",
    component: CardApplicationPage,
    meta: { requiresAuth: true, requiresUser: true },
  },
  {
    path: "/my-applications",
    name: "MyApplications",
    component: CardApplicationHistoryPage,
    meta: { requiresAuth: true, requiresUser: true },
  },
];
 
const router = createRouter({
  history: createWebHistory(),
  routes,
});

const getInitialRoute = () => {
  const token = localStorage.getItem("token");
  if (!token) return { name: "Login" };
 
  try {
    const decoded = store.getters["auth/userRole"];
    return decoded === "ADMIN" ? { name: "AdminDashboard" } : { name: "Dashboard" };
  } catch {
    return { name: "Login" };
  }
};
 
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const isAuthenticated = !!token;
  const isAdmin = store.getters["auth/isAdmin"];
 
  if (to.name === "LandingPage" || to.name === "Login" || to.name === "Register") {
    next();
    return;
  }

  if (to.meta.requiresAdmin && !isAdmin) {
    next({ name: "Dashboard" });
    return;
  }

  if (to.meta.requiresUser && isAdmin) {
    next({ name: "AdminDashboard" });
    return;
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: "Login" });
    return;
  }
 
  next();
});
export default router;

