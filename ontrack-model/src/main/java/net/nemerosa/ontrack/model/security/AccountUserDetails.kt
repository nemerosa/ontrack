package net.nemerosa.ontrack.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

 class AccountUserDetails(
         private val account: Account
 ) : UserDetails, AccountHolder {

     override fun getAuthorities(): Collection<GrantedAuthority> =
             AuthorityUtils.createAuthorityList(account.role.roleName)

     override fun isEnabled(): Boolean = true

     override fun getUsername(): String = account.name

     override fun isCredentialsNonExpired(): Boolean {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun getPassword(): String? = null

     override fun isAccountNonExpired(): Boolean = true

     override fun isAccountNonLocked(): Boolean =true

     override fun getAccount(): Account = account

}
