import { describe, it, expect } from 'vitest'

describe('test app smoke', () => {
  it('runs basic assertion', () => {
    expect(1 + 1).toBe(2)
  })

  it('supports simple string checks', () => {
    const routeName = 'default'
    expect(routeName).toBe('default')
  })
});
