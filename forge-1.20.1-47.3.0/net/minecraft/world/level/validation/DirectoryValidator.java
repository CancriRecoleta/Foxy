//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.validation;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DirectoryValidator {
    private final PathAllowList symlinkTargetAllowList;

    public DirectoryValidator(PathAllowList p_289976_) {
        this.symlinkTargetAllowList = p_289976_;
    }

    public void validateSymlink(Path p_289934_, List<ForbiddenSymlinkInfo> p_289972_) throws IOException {
        Path $$2 = Files.readSymbolicLink(p_289934_);
        if (!this.symlinkTargetAllowList.matches($$2)) {
            p_289972_.add(new ForbiddenSymlinkInfo(p_289934_, $$2));
        }

    }

    public List<ForbiddenSymlinkInfo> validateSave(Path p_289943_, boolean p_289926_) throws IOException {
        final List<ForbiddenSymlinkInfo> $$2 = new ArrayList();

        BasicFileAttributes $$5;
        try {
            $$5 = Files.readAttributes(p_289943_, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException var6) {
            return $$2;
        }

        if (!$$5.isRegularFile() && !$$5.isOther()) {
            if ($$5.isSymbolicLink()) {
                if (!p_289926_) {
                    this.validateSymlink(p_289943_, $$2);
                    return $$2;
                }

                p_289943_ = Files.readSymbolicLink(p_289943_);
            }

            Files.walkFileTree(p_289943_, new SimpleFileVisitor<Path>() {
                private void validateSymlink(Path p_289935_, BasicFileAttributes p_289941_) throws IOException {
                    if (p_289941_.isSymbolicLink()) {
                        DirectoryValidator.this.validateSymlink(p_289935_, $$2);
                    }

                }

                public FileVisitResult preVisitDirectory(Path p_289946_, BasicFileAttributes p_289950_) throws IOException {
                    this.validateSymlink(p_289946_, p_289950_);
                    return super.preVisitDirectory(p_289946_, p_289950_);
                }

                public FileVisitResult visitFile(Path p_289986_, BasicFileAttributes p_289991_) throws IOException {
                    this.validateSymlink(p_289986_, p_289991_);
                    return super.visitFile(p_289986_, p_289991_);
                }
            });
            return $$2;
        } else {
            throw new IOException("Path " + p_289943_ + " is not a directory");
        }
    }
}
