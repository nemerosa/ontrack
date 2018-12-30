let html = "";
const prefix = "https://ams3-delivery-space.ams3.cdn.digitaloceanspaces.com/ontrack/release/docs/";
$.each(releases, function (index, release) {
   html += `
      <tr>
        <td>
            <a href="https://github.com/nemerosa/ontrack/releases/tag/${release}">${release}</a>
        </td>
        <td>
            <a href="${prefix}/${release}/doc/index.html">HTML</a>
             | <a href="${prefix}/${release}/index.pdf">PDF</a>
             | <a href="${prefix}/${release}/javadoc/index.html">Javadoc</a>
        </td>
      </tr>`;
});
$('#ontrack-doc-body').html(html);
