package net.nemerosa.ontrack.casc.context

import org.springframework.stereotype.Component

interface SubSecurityContext : SubCascContext

@Component
class NOPSubSecurityContext : NOPSubCascContext(), SubSecurityContext