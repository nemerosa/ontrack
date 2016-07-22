def project = ontrack.project('prj')
def branch = ontrack.branch('prj', 'test')
def build = ontrack.build('prj', 'test', '1')
// No decorations yet
assert project.messageDecoration == null
assert branch.messageDecoration == null
assert build.messageDecoration == null
// Project decorations
project.config.message('Information', 'INFO')
assert project.messageDecoration == [type: 'INFO', text: 'Information']
// Branch decorations
branch.config.message('Warning', 'WARNING')
assert branch.messageDecoration == [type: 'WARNING', text: 'Warning']
// Build decorations
build.config.message('Error', 'ERROR')
assert build.messageDecoration == [type: 'ERROR', text: 'Error']