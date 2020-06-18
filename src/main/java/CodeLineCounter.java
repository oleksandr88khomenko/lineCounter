import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeLineCounter {
    private final String COMMENT = "//";
    private final String COMMENT_BLOCK_START = "/*";
    private final String COMMENT_BLOCK_END = "*/";
    private final String JAVA_EXTENSION = ".java";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("please put file name or dir path using absolute path");
        }

        try {
            List<FileLineCountHolder> fileLineCountHolderList = new CodeLineCounter().processCounting(args[0]);
            printFileCountHolder(fileLineCountHolderList);
        } catch (IOException e) {
            System.out.println(String.format("error when processing counting, check error msg: %s", e.getMessage()));
        }
    }

    static void printFileCountHolder(List<FileLineCountHolder> fileLineCountHolderList) {
        if (!fileLineCountHolderList.isEmpty()) {
            fileLineCountHolderList.stream().forEach(holder -> {
                        System.out.println("Folder name: " + holder.getDir().toString());
                        System.out.println("Number of files in folder: " + holder.getFileCount());
                        holder.getFileToLineCount().forEach((k, v) -> {
                            System.out.println("File: " + k.getFileName().toString() + " : " + v.intValue());
                        });
                        System.out.println("------------------------------");
                    }
            );
        }
    }

    public List<FileLineCountHolder> processCounting(String inputPath) throws IOException {
        List<FileLineCountHolder> fileLineCountHolderList = new ArrayList<>();
        if (Files.isDirectory(Paths.get(inputPath))) {
            fileLineCountHolderList = countLinesRecursively(inputPath);
        } else {
            String[] fileLines = (String[]) Files.lines(Paths.get(inputPath)).toArray();
            HashMap<Path, Integer> fileToFileCount = new HashMap<>();
            fileToFileCount.put(Paths.get(inputPath), countLines(fileLines));

            FileLineCountHolder fileLineCountHolder = new FileLineCountHolder();
            fileLineCountHolder.setDir(Paths.get(inputPath));
            fileLineCountHolder.setFileCount(fileLines.length);
            fileLineCountHolder.setFileToLineCount(fileToFileCount);

            fileLineCountHolderList.add(fileLineCountHolder);
        }
        return fileLineCountHolderList;
    }

    private List<FileLineCountHolder> countLinesRecursively(String root) throws IOException {
        List<FileLineCountHolder> fileLineCountHolderList = new ArrayList<>();

        Files.walk(Paths.get(root))
                .filter(p -> Files.isDirectory(p))
                .forEach(subDir -> {
                            List<Path> filesInSubdir = null;
                            try {
                                filesInSubdir = getFilesFromFolder(subDir);
                            } catch (IOException e) {
                                System.out.println(String.format("error when opening directory, check error msg: %s", e.getMessage()));
                            }
                            int numberOfFilesInFolder = filesInSubdir.size();

                            filesInSubdir.stream().forEach(file -> {
                                Map<Path, Integer> fileToLineCount = new HashMap<>();

                                String[] linesInFile = new String[0];
                                try {
                                    linesInFile = (String[]) Files.lines(file).toArray();
                                } catch (IOException e) {
                                    System.out.println(String.format("error when opening file, check error msg: %s", e.getMessage()));
                                }

                                fileToLineCount.put(file, countLines(linesInFile));

                                FileLineCountHolder fileLineCountHolder = new FileLineCountHolder();
                                fileLineCountHolder.setDir(subDir);
                                fileLineCountHolder.setFileCount(numberOfFilesInFolder);
                                fileLineCountHolder.setFileToLineCount(fileToLineCount);
                                fileLineCountHolderList.add(fileLineCountHolder);
                            });
                        }
                );

        return fileLineCountHolderList;
    }

    private List<Path> getFilesFromFolder(Path subDir) throws IOException {
        return Files.list(subDir)
                .filter(path -> Files.isRegularFile(path) && path.endsWith(JAVA_EXTENSION))
                .collect(Collectors.toList());
    }

    public int countLines(String[] lines) {
        boolean insideBlock = false;
        int lineCounter = 0;

        for (String line : lines) {
            line = line.trim();

            if (!insideBlock) {
                if (!line.isEmpty() && isNotCommented(line))
                    lineCounter++;

                if (blockStarts(line))
                    insideBlock = true;

            } else {
                if (blockEnds(line))
                    insideBlock = false;
            }
        }
        return lineCounter;
    }

    private boolean isNotCommented(String line) {
        if (simpleComment(line))
            return false;

        if (commentBlock(line))
            return false;

        return true;
    }

    private boolean simpleComment(String line) {
        if (line.length() < COMMENT.length())
            return false;

        return line.startsWith(COMMENT);
    }

    private boolean commentBlock(String line) {
        if (line.length() < COMMENT_BLOCK_START.length())
            return false;

        if (!line.startsWith(COMMENT_BLOCK_START))
            return false;

        int blockCommentPositionEnd = line.indexOf(COMMENT_BLOCK_END);

        if (blockCommentPositionEnd == -1)
            return true;

        line = line.substring(blockCommentPositionEnd + COMMENT_BLOCK_END.length());

        if (line.isEmpty())
            return true;

        return commentBlock(line);
    }

    private boolean blockStarts(String line) {
        return firstMarkerAfterSecond(line, COMMENT_BLOCK_START, COMMENT_BLOCK_END);
    }

    private boolean blockEnds(String line) {
        return firstMarkerAfterSecond(line, COMMENT_BLOCK_END, COMMENT_BLOCK_START);
    }

    private boolean firstMarkerAfterSecond(String line, String firstMarker, String secondMarker) {
        return line.lastIndexOf(firstMarker) > line.lastIndexOf(secondMarker);
    }
}
