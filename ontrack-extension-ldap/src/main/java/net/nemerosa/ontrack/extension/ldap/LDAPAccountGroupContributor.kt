package net.nemerosa.ontrack.extension.ldap;

// @Component
public class LDAPAccountGroupContributor /* implements AccountGroupContributor */ {

//    private final AccountGroupMappingService accountGroupMappingService;
//
//    @Autowired
//    public LDAPAccountGroupContributor(AccountGroupMappingService accountGroupMappingService) {
//        this.accountGroupMappingService = accountGroupMappingService;
//    }
//
//    @Override
//    public Collection<AccountGroup> collectGroups(@NotNull AuthenticatedAccount authenticatedAccount) {
//        // Gets the list of LDAP groups from the account
//        Collection<String> ldapGroups = getLdapGroups(authenticatedAccount);
//        // Maps them to the account groups
//        return ldapGroups.stream()
//                .flatMap(ldapGroup -> accountGroupMappingService.getGroups(LDAPExtensionFeature.LDAP_GROUP_MAPPING, ldapGroup).stream())
//                .collect(Collectors.toList());
//    }
//
//    public static Collection<String> getLdapGroups(@NotNull AuthenticatedAccount authenticatedAccount) {
//        UserDetails userDetails = authenticatedAccount.getUserDetails();
//        if (userDetails instanceof ExtendedLDAPUserDetails) {
//            return ((ExtendedLDAPUserDetails) userDetails).getGroups();
//        } else {
//            return Collections.emptyList();
//        }
//    }

}
