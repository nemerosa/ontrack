import {branchNamePattern} from "@components/mobile/entities/branchNamePattern"

describe('the branch name filter', () => {

    it('is nothing at all until something is typed', () => {
        // `null`, never `''`: the server's own filter tests the argument with
        // `isNullOrBlank`, and the screen must not call itself filtered when it
        // is showing the whole list.
        expect(branchNamePattern('')).toBeNull()
        expect(branchNamePattern('   ')).toBeNull()
        expect(branchNamePattern(undefined)).toBeNull()
    })

    it('ignores the spaces around what was typed', () => {
        expect(branchNamePattern('  main  ')).toEqual('(?i)main')
    })

    it('matches without regard to case', () => {
        // The project list filters through an `ILIKE`, so a phone user typing
        // "MAIN" there finds `main`. `~` is case-sensitive, and the two screens
        // behaving differently would read as one of them being broken.
        expect(branchNamePattern('MAIN')).toEqual('(?i)MAIN')
    })

    it('matches anywhere in the name, not only at the start', () => {
        // Unanchored on purpose: the server matches with `~`, which already
        // searches rather than tests the whole string. Nothing here anchors it.
        const pattern = branchNamePattern('lease')
        expect(pattern.startsWith('^')).toBe(false)
        expect(pattern.endsWith('$')).toBe(false)
    })

    it('takes what was typed literally', () => {
        // The argument reaches Postgres as a POSIX regular expression. A branch
        // name is full of characters that mean something there - `release/1.0`
        // has a dot, and a stray `(` is not a filter that matches nothing but a
        // query that fails outright.
        expect(branchNamePattern('release/1.0')).toEqual('(?i)release/1\\.0')
        expect(branchNamePattern('feature(x)')).toEqual('(?i)feature\\(x\\)')
        expect(branchNamePattern('a+b')).toEqual('(?i)a\\+b')
        expect(branchNamePattern('a|b')).toEqual('(?i)a\\|b')
        expect(branchNamePattern('a[b]')).toEqual('(?i)a\\[b\\]')
        expect(branchNamePattern('a*')).toEqual('(?i)a\\*')
        expect(branchNamePattern('a?')).toEqual('(?i)a\\?')
        expect(branchNamePattern('a{2}')).toEqual('(?i)a\\{2\\}')
        expect(branchNamePattern('a\\b')).toEqual('(?i)a\\\\b')
        expect(branchNamePattern('^a$')).toEqual('(?i)\\^a\\$')
    })

    it('produces a pattern that actually matches what was typed', () => {
        // The escaping above is only right if the result still finds the branch.
        // Checked with JavaScript's own engine, which shares the metacharacters
        // that matter here with the server's.
        const names = ['release/1.0', 'feature(x)', 'a+b', 'main']
        names.forEach(name => {
            const pattern = branchNamePattern(name)
            expect(new RegExp(pattern.replace('(?i)', ''), 'i').test(name)).toBe(true)
        })
    })
})
