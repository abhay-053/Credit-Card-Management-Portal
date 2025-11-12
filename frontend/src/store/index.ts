import { createStore } from "vuex";
import auth, { AuthState } from "./auth";
import user, { UserState } from "./user";
import transactions from "./modules/transactions";
import cards from "./modules/cards";
import merchants from "./modules/merchants";
import bnpl from "./modules/bnpl";
import analytics from "./modules/analytics";
import applications from "./modules/applications";
import userApplications from "./modules/userApplications";
import payments from "./modules/payments";
import cardTypes from "./modules/cardTypes";


export interface RootState {
  auth: AuthState;
  user: UserState;
}

export default createStore({
  modules: {
    auth,
    user,
    transactions,
    cards,
    merchants,
    bnpl,
    analytics,
    applications,
    userApplications,
    cardTypes,
    payments,
  },
});
