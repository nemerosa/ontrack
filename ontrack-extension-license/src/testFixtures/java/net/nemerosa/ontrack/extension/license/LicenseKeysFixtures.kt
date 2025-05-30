package net.nemerosa.ontrack.extension.license

object LicenseKeysFixtures {

    /**
     * Resource path to the testing license signature key
     */
    const val TEST_SIGNATURE_RESOURCE_PATH = "/testing/keys/embedded.key"

    /**
     * An unlimited license key (no max projects, environments, no max)
     *
     * {"name":"XL","assignee":"Nemerosa Local","validUntil":null,"maxProjects":0,"features":[{"id":"extension.environments","enabled":true,"data":[{"name":"maxEnvironments","value":"0"}]}]}
     */
    const val UNLIMITED =
        "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXdJbjFkZlYxOSIsInNpZ25hdHVyZSI6Ik1FVUNJUURiNmd5WkZNbktINUxXdFhTODFtdFlwMzN2STFRSFNmNldTREdGd2UxUnBRSWdOekMwditTd29ad1pVZ01CcFlDeCtmb2g0L0EwSnVhd0hYU2V5UW1sQ3dvPSJ9"

    /**
     * A limited license key (no max projects, environments, max environments)
     *
     * {"name":"XL","assignee":"Nemerosa Local","validUntil":null,"maxProjects":0,"features":[{"id":"extension.environments","enabled":true,"data":[{"name":"maxEnvironments","value":"10"}]}]}
     */
    const val LIMITED_ENVIRONMENTS =
        "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXhNQ0o5WFgxZGZRPT0iLCJzaWduYXR1cmUiOiJNRVFDSUIvTUpEK1JSY1NyNGxObit4MmdTbFduYmVHZkhMRGxKdkN6cVdScXZ6S0RBaUErNkVtcTBsY3EzQm1OYWk4TGk2a292K0pmUmM4eDhMUFNWem1pKzM5a3F3PT0ifQ=="

}