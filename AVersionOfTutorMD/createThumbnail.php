<?php
// Name: createThumbnail.php
// Authors: Tharun V, Jason Y, Ivy G, Natalie S
// Purpose: Takes entered profile images and creates thumbnails of set sizes
// Link image type to correct image loader and saver
// - makes it easier to add additional types later on
// - makes the function easier to read
const IMAGE_HANDLERS = [
    IMAGETYPE_JPEG => [
        'load' => 'imagecreatefromjpeg',
        'save' => 'imagejpeg',
        'quality' => 100
    ],
    IMAGETYPE_PNG => [
        'load' => 'imagecreatefrompng',
        'save' => 'imagepng',
        'quality' => 0
    ],
    IMAGETYPE_GIF => [
        'load' => 'imagecreatefromgif',
        'save' => 'imagegif'
    ]
];

/**
 * @param $src - a valid file location
 * @param $dest - a valid file target
 * @param $targetWidth - desired output width
 * @param $targetHeight - desired output height or null
 */
function createThumbnail($src, $dest, $targetWidth, $targetHeight = null) {

    // 1. Load the image from the given $src
    // - see if the file actually exists
    // - check if it's of a valid image type
    // - load the image resource

    // get the type of the image
    // we need the type to determine the correct loader
    $type = exif_imagetype($src);

    // if no valid type or no handler found -> exit
    if (!$type || !IMAGE_HANDLERS[$type]) {
        return null;
    }// if

    // load the image with the correct loader
    $image = call_user_func(IMAGE_HANDLERS[$type]['load'], $src);

    // no image found at supplied location -> exit
    if (!$image) {
        return null;
    }// if


    // 2. Create a thumbnail and resize the loaded $image
    // - get the image dimensions
    // - define the output size appropriately
    // - create a thumbnail based on that size
    // - set alpha transparency for GIFs and PNGs
    // - draw the final thumbnail

    // get original image width and height
    $width = imagesx($image);
    $height = imagesy($image);

    // define defaults for thumbnail
    $startX = 0;
    $startY = 0;
    $src_w = $width;
    $src_h = $height;


    // maintain aspect ratio when no height set
    if ($targetHeight == null) {

        // get width to height ratio
        $ratio = $width / $height;

        // if is portrait
        // use ratio to scale height to fit in square
        if ($width > $height) {
            $targetHeight = floor($targetWidth / $ratio);
        }// if
        // if is landscape
        // use ratio to scale width to fit in square
        else {
            $targetHeight = $targetWidth;
            $targetWidth = floor($targetWidth * $ratio);
        }// else
    }  else {
      // Calculate starting positions and copy area 
      // to not distort the thumbnail image ratio.
      // get width to height ratio
      $ratio = $width / $height;
      $targetRatio = $targetWidth / $targetHeight;
      $combinedRatio = $ratio / $targetRatio;

      // Cut X wise in source to avoid thumb image distortion
      if ($combinedRatio >= 1) {
        $thumbRatio = $height / $targetHeight;
        $startX = (int) (($width - ($thumbRatio * $targetWidth)) / 2);
        $src_w = (int) ($thumbRatio * $targetWidth);
        $startY = 0;
        $src_h =  (int) $height;
      }// if
      // Cut Y wise in source to avoid thumb image distortion
      else {
        $thumbRatio = $width / $targetWidth;
        $startY = (int) (($height - ($thumbRatio * $targetHeight)) / 2);
        $src_h =  (int) ($thumbRatio * $targetHeight);
        $startX = 0;
        $src_w = (int) $width;
      }// else
    }// else

    // create duplicate image based on calculated target size
    $thumbnail = imagecreatetruecolor($targetWidth, $targetHeight);

    // set transparency options for GIFs and PNGs
    if ($type == IMAGETYPE_GIF || $type == IMAGETYPE_PNG) {

        // make image transparent
        imagecolortransparent(
            $thumbnail,
            imagecolorallocate($thumbnail, 0, 0, 0)
        );

        // additional settings for PNGs
        if ($type == IMAGETYPE_PNG) {
            imagealphablending($thumbnail, false);
            imagesavealpha($thumbnail, true);
        }// if
    }// if

    // copy entire source image to duplicate image and resize
   /* imagecopyresampled(
        $thumbnail,
        $image,
        0, 0, 0, 0,
        $targetWidth, $targetHeight,
        $width, $height
    );*/

imagecopyresampled(
        $thumbnail,
        $image,
        0, 0, $startX, $startY,
        $targetWidth, $targetHeight,
        $src_w, $src_h
    );


    // 3. Save the $thumbnail to disk
    // - call the correct save method
    // - set the correct quality level

    // save the duplicate version of the image to disk
    return call_user_func(
        IMAGE_HANDLERS[$type]['save'],
        $thumbnail,
        $dest,
        IMAGE_HANDLERS[$type]['quality']
    );
}// createThumbnail
?>