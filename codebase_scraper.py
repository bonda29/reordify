from codebase_to_text import CodebaseToText

cbt = CodebaseToText(
    input_path="https://github.com/bonda29/reordify",
    output_path=r"C:\Users\bonda\Downloads\new 26.txt",
    output_type="txt",
    verbose=False,        # or True if you want log output
    exclude_hidden=True  # or True to skip hidden files/directories
)
cbt.get_file()
