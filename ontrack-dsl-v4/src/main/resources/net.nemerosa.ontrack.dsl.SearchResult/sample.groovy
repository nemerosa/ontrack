ontrack.project('prj')
def results = ontrack.search('prj')
assert results.size() == 1
assert results[0].title == 'Project prj'
assert results[0].page == 'https://host/#/project/1'
assert results[0].page == 'https://host/#/project/1'