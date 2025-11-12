import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import ApplyCardView from "../../src/views/Users/CardApplicationPage.vue";
import { createStore } from "vuex";

// ------------------
// Mock services
// ------------------
vi.mock("../../src/services/cards-service", () => ({
  getCardTypes: vi.fn(),
}));

vi.mock("../../src/services/userService", () => ({
  getUserProfile: vi.fn(),
}));

vi.mock("../../src/services/cardApplicationService", () => ({
  applyForCard: vi.fn(),
}));

vi.mock("../../src/utils/getTokenData", () => ({
  getUserIdFromToken: vi.fn(),
}));

// Import mocks AFTER vi.mock calls
import { getCardTypes } from "../../src/services/cards-service";
import { getUserProfile } from "../../src/services/userService";
import { applyForCard } from "../../src/services/cardApplicationService";
import { getUserIdFromToken } from "../../src/utils/getTokenData";

// ------------------
// Test suite
// ------------------
describe("ApplyCardView.vue", () => {
  const mockUser = {
    id: 42,
    name: "John Doe",
    email: "john@example.com",
    bnplEligible: true,
  };

  const mockCards = [
    {
      id: 1,
      name: "AmEx Gold",
      networkType: "AMEX",
      description: "Great rewards",
      minCardLimit: 50000,
      maxCardLimit: 200000,
    },
    {
      id: 2,
      name: "Visa Platinum",
      networkType: "VISA",
      description: "Worldwide acceptance",
      minCardLimit: 30000,
      maxCardLimit: 150000,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (getUserIdFromToken as unknown as jest.MockInstance<any, any>).mockReturnValue(mockUser.id);
    (getUserProfile as unknown as jest.MockInstance<any, any>).mockResolvedValue({ data: mockUser });
    (getCardTypes as unknown as jest.MockInstance<any, any>).mockResolvedValue(mockCards);
  });

  it("renders header correctly", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    expect(wrapper.find("h1").text()).toBe("Apply for Credit Card");
    expect(wrapper.find(".btn-history").exists()).toBe(true);
  });

  it("fetches and renders card options", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    const cards = wrapper.findAll(".option-card");
    expect(cards.length).toBe(2);
    expect(cards[0].text()).toContain("AmEx Gold");
    expect(cards[1].text()).toContain("Visa Platinum");
  });

  it("selects a card and shows the form", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    const firstCard = wrapper.findAll(".option-card")[0];
    await firstCard.trigger("click");

    expect(wrapper.vm.selectedCard?.name).toBe("AmEx Gold");

    // Form should appear
    expect(wrapper.find(".application-form").exists()).toBe(true);

    // ✅ Fixed: look for h2 inside the form section
    const formHeader = wrapper.find(".application-form h2");
    expect(formHeader.text()).toContain("AmEx Gold Application");
  });

  it("shows applicant information", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    const firstCard = wrapper.findAll(".option-card")[0];
    await firstCard.trigger("click");

    const info = wrapper.find(".form-row.info");
    expect(info.text()).toContain("John Doe");
    expect(info.text()).toContain("john@example.com");
  });

  it("submits application with correct data", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    const firstCard = wrapper.findAll(".option-card")[0];
    await firstCard.trigger("click");

    // set credit limit
    const input = wrapper.find("input[type='number']");
    await input.setValue(60000);

    // mock router
    wrapper.vm.$router = { push: vi.fn() } as any;

    await wrapper.find("form").trigger("submit.prevent");

    expect(applyForCard).toHaveBeenCalledWith({
      userId: mockUser.id,
      cardTypeId: 1,
      requestLimit: 60000,
    });
  });

  it("hides application form if no card is selected", async () => {
    const wrapper = mount(ApplyCardView);
    await flushPromises();

    // Without selecting a card
    expect(wrapper.find(".application-form").exists()).toBe(false);
  });
});
