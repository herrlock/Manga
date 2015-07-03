
namespace MD
{
	class Run
	{
		static void Main()
		{
            System.Diagnostics.Process process = new System.Diagnostics.Process();
            System.Diagnostics.ProcessStartInfo startInfo = new System.Diagnostics.ProcessStartInfo();
            startInfo.WindowStyle = System.Diagnostics.ProcessWindowStyle.Hidden;
            startInfo.FileName = "cmd.exe";
            startInfo.Arguments = "/C java -cp \"lib/*\" de.herrlock.manga.Ctrl 2>>log/err.log";
            process.StartInfo = startInfo;
            process.Start();
		}
	}
}