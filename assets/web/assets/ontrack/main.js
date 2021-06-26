let html = "";
const latestRelease = releases[0];
const prefix = "https://static.nemerosa.net/ontrack/release";

html += `
      <tr>
        <td>
            <span class="ontrack-doc-latest">Latest</span> (${latestRelease})
        </td>
        <td>
            <a href="${prefix}/latest/docs/doc/index.html">HTML</a>
             | <a href="${prefix}/latest/docs/index.pdf">PDF</a>
             | <a href="${prefix}/latest/docs/javadoc/index.html">Javadoc</a>
        </td>
      </tr>`;

$.each(releases, function (index, release) {
   html += `
      <tr>
        <td>
            <a href="https://github.com/nemerosa/ontrack/releases/tag/${release}">${release}</a>
        </td>
        <td>
            <a href="${prefix}/${release}/docs/doc/index.html">HTML</a>
             | <a href="${prefix}/${release}/docs/index.pdf">PDF</a>
             | <a href="${prefix}/${release}/docs/ontrack-javadoc.zip">Javadoc ZIP</a>
        </td>
      </tr>`;
});
$('#ontrack-doc-body').html(html);
