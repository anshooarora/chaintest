<#if build.isBDD()>
  <#assign
    title1 = 'Features', title2 = 'Scenarios', title3 = 'Steps', tagStatsLevel = 1
  >
<#elseif build.testRunner == 'testng'>
  <#if build.runStats?size gte 3>
    <#assign
      title1 = 'Suites', title2 = 'Classes', title3 = 'Methods', tagStatsLevel = 2
    >
    <#else>
      <#assign
        title1 = 'Classes', title2 = 'Methods', tagStatsLevel = 1
      >
  </#if>
<#elseif build.testRunner == 'junit' || build.testRunner == 'junit5' || build.testRunner == 'junit-jupiter'>
  <#assign
    title1 = 'Classes', title2 = 'Methods', tagStatsLevel = 1
  >
<#else>
    <#assign
        title1 = 'Tests', title2 = 'Methods', title3 = 'Events', tagStatsLevel = 1
    >
</#if>
<#if build.runStats?? && build.runStats?has_content && (tagStatsLevel gte build.runStats?size) && build.runStats?size gte 1>
    <#assign tagStatsLevel = build.runStats?size - 1>
</#if>

<!DOCTYPE html>
<html lang="en" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="x-apple-disable-message-reformatting">
    <title>ChainTest</title>
    <style type="text/css">
      body { font-family: -apple-system, system-ui, "Helvetica Neue", "Segoe UI", Arial, sans-serif; font-size: 13px; }
      .bg-passed { background-color: rgb(168, 217, 167); }
      .text-passed { color: #79B530; }
      .bg-failed,.bg-undefined { background-color: rgb(254, 205, 204); }
      .text-failed,.text-undefined { color: #e64b5d; }
      .bg-skipped { background-color: #eee; }
      .text-skipped { color: #e6e04c; }
      .tag { background-color:#f6f7f9; }
      pre {white-space: pre-wrap; margin-bottom: 0; max-height: 15rem; overflow-y: auto; }
    </style>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" yahoo="fix" bgcolor="#F7F8F9" style="box-sizing:border-box;margin:0;padding:0;width:100%;word-break:break-word;-webkit-font-smoothing:antialiased;">
    <!-- title -->
    <table class="wrapper" cellpadding="0" cellspacing="0" role="presentation" width="100%">
      <tr>
        <td align="center">
          <table>
            <tr>
              <td width="650">
                <table style="padding: 20px;">
                  <tr>
                    <td>
                      <span class="bg-${build.result?lower_case}" style="margin-right:10px;padding:0px 5px;">${build.result}</span>
                      <span style="">${build.startedAt?number_to_datetime} &middot; ${build.durationPretty}</span>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>

    <!-- stats -->
    <#if build.runStats?? && build.runStats?size != 0>
    <table align="center" width="650" style="margin-bottom:2px;">
	  <tbody>
	    <tr>
	      <td align="center" bgcolor="#FFFFFF">
	        <table width="100%">
	          <tbody>
	            <tr>
	              <td width="100" style="padding:30px 40px 20px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;">Tests</p>
	                <h1 style="font-size:34px;">${build.runStats[0].total}</h1>
	              </td>
                <td width="100" style="padding:30px 40px 20px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;color:#79B530">Passed</p>
	                <h1 style="font-size:34px;">${build.runStats[0].passed}</h1>
	              </td>
                <td width="100" style="padding:30px 40px 20px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;color:#e64b5d">Failed</p>
	                <h1 style="font-size:34px;">${build.runStats[0].failed}</h1>
	              </td>
                <td width="100" style="padding:30px 40px 20px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;color:#e6e04c">Skipped</p>
	                <h1 style="font-size:34px;">${build.runStats[0].skipped}</h1>
	              </td>
	            </tr>
	          </tbody>
	        </table>
	      </td>
	    </tr>
	  </tbody>
	</table>
	</#if>

    <!-- tags -->
    <#if build.tagStats??>
    <table width="100%">
      <tr>
        <td align="center">
          <table width="650">
            <tr>
              <td bgcolor="#FFFFFF" style="padding:20px 40px;font-size:14px;">
                <span style="font-size:15px;font-weight:500;">Tags</span>
                <table><tr><td style="height:10px;"></td></tr></table>
                <table>
                  <tr style="font-weight:500;">
                    <td width="250">Name</td>
                    <td width="23"></td>
                    <td style="" width="60">Pass</td>
                    <td class="padding" width="10"></td>
                    <td style="" width="60">Fail</td>
                    <td class="padding" width="10"></td>
                    <td style="" width="60">Skip</td>
                  </tr>
                  <#list build.tagStats as tag>
                  <#if tag.depth == tagStatsLevel>
                  <tr style="height:10px;"></tr>
                  <tr>
                    <td width="350">${tag.name}</td>
                      <td width="23"></td>
                      <td style="" width="60">${tag.passed}</td>
                      <td class="padding" width="10"></td>
                      <td style="" width="60">${tag.failed}</td>
                      <td class="padding" width="10"></td>
                      <td style="" width="60">${tag.skipped}</td>
                  </tr>
                  </#if>
                  </#list>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    </#if>

    <!-- tests -->
    <table width="100%">
      <tr>
        <td align="center">
          <#list tests as test>
          <#if test.result != 'PASSED'>
          <table width="650">
            <tr>
              <td bgcolor="#FFF" style="padding:10px 20px;">
                <table>
                  <tr>
                    <td width="650px" style="padding:10px;">
                      <p class="text-${test.result?lower_case}" style="font-size:14px;font-weight:500;margin:0;margin-bottom:5px;">
                        ${test.name}
                      <p style="font-size:12px;margin:0;">
                        <span>${test.startedAt?number_to_datetime} / ${test.durationPretty}</span>
                      </p>
                      <#if test.error??>
<pre>${test.error}</pre>
                      </#if>
                    </td>
                  </tr>
                  <#list test.children as child>
                  <tr>
                    <td width="650px" class="bg-${child.result?lower_case}" style="padding:7px 10px;">
                      <p style="font-size:13px;margin:0;">
                        ${child.name}
                      </p>
                    </td>
                  </tr>
                  <#list child.children as leaf>
                  <tr>
                    <td width="650px" class="bg-${leaf.result?lower_case}" style="padding:7px 20px;">
                      <p style="font-size:13px;margin:0;">
                        ${leaf.name}
                        <#if leaf.error??>
<pre>${leaf.error}</pre>
                        </#if>
                      </p>
                    </td>
                  </tr>
                  </#list>
                  <tr><td style="height:10px"></td></tr>
                  </#list>
                </table>
              </td>
            </tr>
          </table>
          </#if>
          </#list>
        </td>
      </tr>
    </table>
    <table><tr><td height="100"></td></tr></table>
  </body>
</html>