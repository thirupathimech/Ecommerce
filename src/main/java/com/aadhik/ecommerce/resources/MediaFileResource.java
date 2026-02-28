package com.aadhik.ecommerce.resources;

import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("files")
public class MediaFileResource {

    @Inject
    private CatalogService catalogService;

    @GET
    @Path("{id}")
    @Produces({"image/*", "video/*", "application/pdf", "application/octet-stream"})
    public Response getFile(@PathParam("id") Long id) {
        MediaFile mediaFile = catalogService.getMediaFile(id);
        if (mediaFile == null || mediaFile.getData() == null) {
            throw new NotFoundException("File not found");
        }

        return Response.ok(mediaFile.getData(), mediaFile.getContentType())
                .header("Content-Disposition", "inline; filename=\"" + mediaFile.getFileName() + "\"")
                .build();
    }
}
