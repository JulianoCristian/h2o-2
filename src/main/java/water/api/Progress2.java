package water.api;

import water.*;
import water.api.RequestServer.API_VERSION;

public class Progress2 extends Request2 {
  static final int API_WEAVER = 1; // This file has auto-gen'd doc & json fields
  static public DocGen.FieldDoc[] DOC_FIELDS; // Initialized from Auto-Gen code.

  // This Request supports the HTML 'GET' command, and this is the help text
  // for GET.
  static final String DOC_GET = "Track progress of an ongoing Job";

  @API(help = "The Job id being tracked.", json = true, filter = Default.class)
  public Key job_key;

  @API(help = "The destination key being produced.", json = true, required = true, filter = Default.class)
  public Key destination_key;

  @API(help = "")
  public float progress = 0.0f;

  public static String jsonUrl(Key jobKey, Key destKey) {
    return "2/Progress2.json?job_key=" + jobKey + "&destination_key=" + destKey;
  }

  public static Response redirect(Request req, Key jobkey, Key dest) {
    return new Response(Response.Status.redirect, req, -1, -1, "/2/Progress2", "job_key", jobkey, "destination_key", dest);
  }

  @Override protected Response serve() {
    Job jjob = null;
    if(job_key != null)
      jjob = Job.findJob(job_key);
    if( jjob != null && jjob.exception != null ) {
      return Response.error(jjob.exception);
    }

    if(jjob == null || jjob.end_time > 0 || jjob.cancelled())
      return jobDone(jjob, destination_key);

    return jobInProgress(jjob, destination_key);
  }

  /** Return {@link Response} for finished job. */
  protected Response jobDone(final Job job, final Key dst) {
    return Inspect2.redirect(this, dst.toString());
  }

  /** Return default progress {@link Response}. */
  protected Response jobInProgress(final Job job, final Key dst) {
    progress = job.progress();
    return new Response(Response.Status.poll, this, (int) (100 * job.progress()), 100, "job_key", job_key.toString(), "destination_key", destination_key.toString());
  }

  @Override public boolean toHTML(StringBuilder sb) {
    Job jjob = Job.findJob(job_key);
    DocGen.HTML.title(sb, jjob != null ? jjob.description : null);
    DocGen.HTML.section(sb, destination_key.toString());
    return true;
  }

  @Override protected boolean log() {
    return false;
  }

  @Override public API_VERSION[] supportedVersions() {
    return SUPPORTS_ONLY_V2;
  }
}
