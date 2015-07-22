println ontrack.build('ontrack', 'master', '140c55e').getChangeLog(
        ontrack.build('ontrack', 'master', '8474ff8')
).exportIssues(
        format: 'text',
        groups: [
                'Bugs'        : ['defect'],
                'Features'    : ['feature'],
                'Enhancements': ['enhancement'],
        ],
        exclude: ['design', 'delivery']
)
