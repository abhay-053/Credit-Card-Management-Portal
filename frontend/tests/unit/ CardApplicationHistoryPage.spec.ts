import { describe, it, expect, vi, beforeEach } from "vitest";
import { shallowMount, mount } from "@vue/test-utils";
import MyApplicationsView from "../../src/views/Users/CardApplicationHistoryPage.vue";
import { createStore } from "vuex";

// Mock action
const mockFetchAll = vi.fn();

// Helper to create store for each test

interface StoreOverrides {
  allApplications?: any[];
  isLoading?: boolean;
  error?: string;
}

const createVuexStore = (overrides: StoreOverrides = {}) => {
  return createStore({
    modules: {
      userApplications: {
        namespaced: true,
        state: () => ({
          allApplications: overrides.allApplications || [],
          isLoading: overrides.isLoading || false,
          error: overrides.error || "",
        }),
        getters: {
          allApplications: (state) => state.allApplications,
          isLoading: (state) => state.isLoading,
          error: (state) => state.error,
        },
        actions: {
          fetchAll: mockFetchAll,
        },
      },
    },
  });
};

describe("MyApplicationsView.vue", () => {
  beforeEach(() => {
    mockFetchAll.mockClear();
  });

  it("renders loading state", () => {
    const store = createVuexStore({ isLoading: true });
    const wrapper = mount(MyApplicationsView, {
      global: { plugins: [store] },
    });

    expect(wrapper.text()).toContain("Loading applications...");
  });

  it("renders error state", () => {
    const store = createVuexStore({ error: "Something went wrong" });
    const wrapper = mount(MyApplicationsView, {
      global: { plugins: [store] },
    });

    expect(wrapper.text()).toContain("Something went wrong");
  });

  it("renders list of applications", () => {
    const store = createVuexStore({
      allApplications: [
        {
          id: 1,
          cardTypeName: "AmEx Diamond",
          cardNetworkType: "AMEX",
          requestedLimit: 50000,
          status: "Under Review",
          statusText: "Application received",
          appliedDate: "2025-09-25",
          reviewerName: "",
          reviewDate: "",
        },
      ],
    });

    const wrapper = mount(MyApplicationsView, {
      global: { plugins: [store] },
    });

    expect(wrapper.findAll(".application-card").length).toBe(1);
    expect(wrapper.text()).toContain("AmEx Diamond");
  });

  it("calls fetchAll on created hook", () => {
    const store = createVuexStore();
    mount(MyApplicationsView, {
      global: { plugins: [store] },
    });

    expect(mockFetchAll).toHaveBeenCalled();
  });
});
