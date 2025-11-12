import {Dropdown, Input, Space, Spin, Typography} from "antd";
import {useRefData} from "@components/providers/RefDataProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {gql} from "graphql-request";
import {FaSearch} from "react-icons/fa";
import debounce from "lodash.debounce";
import SearchResultType from "@components/search/SearchResultType";
import SearchResult from "@components/search/SearchResult";

export default function SearchBox({style}) {
    const {searchResultTypes} = useRefData();
    const client = useGraphQLClient();

    const [searchValue, setSearchValue] = useState('');
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [searchResults, setSearchResults] = useState({});
    const [searchingTypes, setSearchingTypes] = useState(new Set());

    // Store abort controllers for each search type
    const abortControllersRef = useRef({});

    // Cancel all ongoing searches
    const cancelAllSearches = useCallback(() => {
        Object.values(abortControllersRef.current).forEach(controller => {
            if (controller) {
                controller.abort();
            }
        });
        abortControllersRef.current = {};
        setSearchingTypes(new Set());
    }, []);

    // Perform search for a specific type
    const searchForType = useCallback(async (type, token) => {
        // Create a new abort controller for this search
        const controller = new AbortController();
        abortControllersRef.current[type.id] = controller;

        // Mark this type as searching
        setSearchingTypes(prev => new Set([...prev, type.id]));

        try {
            const data = await client.request(
                gql`
                    query SearchByType($type: String!, $token: String!) {
                        search(type: $type, token: $token, offset: 0, size: 5) {
                            pageInfo {
                                totalSize
                            }
                            pageItems {
                                type {
                                    id
                                    name
                                    description
                                }
                                title
                                description
                                data
                                accuracy
                                page
                            }
                        }
                    }
                `,
                {
                    type: type.id,
                    token: token,
                },
                {
                    signal: controller.signal
                }
            );

            // Update results for this type
            setSearchResults(prev => ({
                ...prev,
                [type.id]: {
                    type: type,
                    items: data.search.pageItems,
                    totalSize: data.search.pageInfo.totalSize,
                }
            }));
        } catch (error) {
            if (error.name !== 'AbortError') {
                console.error(`Search error for type ${type.id}:`, error);
                setSearchResults(prev => ({
                    ...prev,
                    [type.id]: {
                        type: type,
                        items: [],
                        totalSize: 0,
                        error: true,
                    }
                }));
            }
        } finally {
            // Remove this type from searching set
            setSearchingTypes(prev => {
                const next = new Set(prev);
                next.delete(type.id);
                return next;
            });
            delete abortControllersRef.current[type.id];
        }
    }, [client]);

    // Perform parallel searches across all types
    const performSearches = useCallback((token) => {
        if (!token || token.length < 3) {
            return;
        }

        // Cancel any ongoing searches
        cancelAllSearches();

        // Clear previous results
        setSearchResults({});

        // Show dropdown
        setDropdownOpen(true);

        // Start parallel searches for all types
        searchResultTypes.forEach(type => {
            searchForType(type, token);
        });
    }, [searchResultTypes, searchForType, cancelAllSearches]);

    // Debounced search function
    const debouncedSearch = useMemo(
        () => debounce((token) => {
            performSearches(token);
        }, 500),
        [performSearches]
    );

    // Handle input change
    const handleChange = (e) => {
        const value = e.target.value;
        setSearchValue(value);

        if (value.length >= 3) {
            debouncedSearch(value);
        } else if (value.length === 0) {
            // Clear field: cancel searches and hide dropdown
            cancelAllSearches();
            setSearchResults({});
            setDropdownOpen(false);
        } else {
            // Less than 3 characters: cancel searches but keep value
            cancelAllSearches();
            setSearchResults({});
            setDropdownOpen(false);
        }
    };

    // Handle search submit (Enter key or search icon click)
    const handleSearch = (value) => {
        if (value && value.length >= 3) {
            performSearches(value);
        }
    };

    // Cleanup on unmount
    useEffect(() => {
        return () => {
            cancelAllSearches();
            debouncedSearch.cancel();
        };
    }, [cancelAllSearches, debouncedSearch]);

    // Build dropdown menu content
    const dropdownMenu = {
        items: [],
    };

    // Add results grouped by type
    searchResultTypes.forEach(type => {
        const typeResults = searchResults[type.id];
        const isSearching = searchingTypes.has(type.id);

        dropdownMenu.items.push({
            key: type.id,
            type: 'group',
            label: (
                <Space direction="horizontal">
                    <SearchResultType type={type} displayName={true} popover={false}/>
                    {isSearching && <Spin size="small"/>}
                    {!isSearching && typeResults && (
                        <Typography.Text type="secondary">
                            ({typeResults.totalSize})
                        </Typography.Text>
                    )}
                </Space>
            ),
            children: isSearching ? [
                {
                    key: `${type.id}-loading`,
                    label: (
                        <Space>
                            <Spin size="small"/>
                            <Typography.Text type="secondary">Searching...</Typography.Text>
                        </Space>
                    ),
                    disabled: true,
                }
            ] : typeResults && typeResults.items.length > 0 ? typeResults.items.map((item, idx) => ({
                key: `${type.id}-${idx}`,
                label: (
                    <div>
                        <SearchResult result={item} showType={false}/>
                    </div>
                ),
                onClick: () => {
                    if (item.page) {
                        window.location.href = item.page;
                    }
                }
            })) : typeResults && !typeResults.error ? [
                {
                    key: `${type.id}-empty`,
                    label: <Typography.Text type="secondary">No results</Typography.Text>,
                    disabled: true,
                }
            ] : typeResults && typeResults.error ? [
                {
                    key: `${type.id}-error`,
                    label: <Typography.Text type="danger">Search error</Typography.Text>,
                    disabled: true,
                }
            ] : [],
        });
    });

    return (
        <>
            <Dropdown
                menu={dropdownMenu}
                open={dropdownOpen && dropdownMenu.items.length > 0}
                onOpenChange={setDropdownOpen}
                trigger={[]}
                overlayStyle={{
                    maxHeight: '500px',
                    overflow: 'auto',
                    minWidth: '400px',
                }}
            >
                <Input.Search
                    style={style}
                    placeholder="Search..."
                    value={searchValue}
                    onChange={handleChange}
                    onSearch={handleSearch}
                    allowClear={true}
                    prefix={<FaSearch/>}
                />
            </Dropdown>
        </>
    );
}