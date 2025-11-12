// tests/setup.ts
import { expect } from 'vitest'
import * as jestDomMatchers from '@testing-library/jest-dom/matchers'

// Normalize exports (handles both default and named-export cases)
const matchers: Record<string, any> =
  (jestDomMatchers && (jestDomMatchers as any).default)
    ? (jestDomMatchers as any).default
    : (jestDomMatchers as any)

if (matchers && Object.keys(matchers).length > 0) {
  expect.extend(matchers)
} else {
  // Not fatal — tests still run with Vitest's built-in expect
  // eslint-disable-next-line no-console
  console.warn('[tests/setup] jest-dom matchers not loaded; proceeding without them.')
}
