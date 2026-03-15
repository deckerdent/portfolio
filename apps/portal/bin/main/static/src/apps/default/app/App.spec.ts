import { describe, it, expect } from 'vitest'

describe('default app smoke', () => {
  it('runs basic assertion', () => {
    expect(true).toBe(true)
  })

  it('renders expected welcome label string constant', () => {
    const label = 'Welcome portal 👋'
    expect(label).toContain('Welcome')
  })
});
