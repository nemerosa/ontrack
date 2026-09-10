import "@testing-library/jest-dom"
import {render, screen} from "@testing-library/react"
import MobileBottomNav from "@components/mobile/layout/MobileBottomNav"

const usePathname = jest.fn()
jest.mock("next/navigation", () => ({usePathname: () => usePathname()}))

const renderAt = (pathname) => {
    usePathname.mockReturnValue(pathname)
    render(<MobileBottomNav/>)
}

describe('MobileBottomNav', () => {

    it('offers its destinations, and no dead ends', () => {
        renderAt('/mobile')
        expect(screen.getByTestId('mobile-nav-home')).toHaveAttribute('href', '/mobile')
        expect(screen.getByTestId('mobile-nav-projects')).toHaveAttribute('href', '/mobile/projects')
        // Search led to a placeholder; global search stays desktop-only for 5.4.
        expect(screen.queryByTestId('mobile-nav-search')).toBeNull()
    })

    it('marks the current tab, for the eye and for a screen reader alike', () => {
        renderAt('/mobile/projects')
        expect(screen.getByTestId('mobile-nav-projects')).toHaveAttribute('aria-current', 'page')
        expect(screen.getByTestId('mobile-nav-home')).not.toHaveAttribute('aria-current')
    })

    it('marks nothing on a screen belonging to no tab', () => {
        renderAt('/mobile/desktop-only')
        expect(screen.queryByRole('link', {current: 'page'})).toBeNull()
    })
})
